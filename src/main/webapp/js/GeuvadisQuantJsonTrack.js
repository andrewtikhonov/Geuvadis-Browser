Genoverse.Track.GeuvadisQuantJsonTrack = Genoverse.Track.extend({

    name           : 'QuantJsonTrack',
    url            : 'exon?r=__CHR__:__START__-__END__',
    height         : 100,
    featureHeight  : 98,
    labels         : 'overlay',
    resizable      : false,
    dataType       : 'json',
    featureColor   : 'rgba(200,0,0,0.8)',
    searchUrl      : 'www.google.com/search?q=',
    enableDataView : true,

    makeFeature: function (data) {
        //alert("Geuvadis makeFeature ");


        // link + protein coding
        // http://browser.1000genomes.org/Homo_sapiens/Location/View?db=core;r=1:1177739-1178131

        data.item       = data.id;
        data.id         = data.item + "_" + data.start + "_" + data.end + "_" + data.score;
        data.start      = parseInt(data.start, 10);
        data.end        = parseInt(data.end, 10);
        data.score      = parseFloat(data.score);
        data.logx10     = Math.log(data.score + 1) * 10;
        data.color      = this.featureColor;

        return data;
    },

    parseData: function (data) {
        //alert("Geuvadis parseData data.length=" + data.length);

        for (var i=0; i<data.length-2; i+=3) {

            var feature1 = this.makeFeature(data[i]);   // min
            var feature2 = this.makeFeature(data[i+1]); // avg
            var feature3 = this.makeFeature(data[i+2]); // max

            var min = feature1.score;
            var avg = feature2.score;
            var max = feature3.score;

            feature1.max = max;
            feature1.avg = avg;
            feature1.min = min;

            feature2.max = max;
            feature2.avg = avg;
            feature2.min = min;

            feature3.max = max;
            feature3.avg = avg;
            feature3.min = min;

            this.insertFeature(feature1);
            this.insertFeature(feature2);
            this.insertFeature(feature3);

        }
    },

    draw: function (features, context, scale) {
        //alert("Geuvadis draw features.length=" + features.length);

        for (var i=0; i<features.length; i++) {
            var feature = features[i];
            context.fillStyle = feature.color; //'black'

            context.fillRect(Math.round(feature.position[scale].X),
                    this.featureHeight - Math.round(feature.position[scale].Y),
                    feature.position[scale].width, -Math.round(feature.logx10));
        }
    },

    populateMenu : function (feature) {
        return {
            title                  : "<a target=_blank href=\"" + this.searchUrl + (this.name2Term != null ? (this.name2Term(feature.item)) : feature.item) + "\"> " + feature.item + "</a>",
            Location               : feature.chr + ':' + feature.start + '-' + feature.end,
            Max                    : feature.max,
            Avg                    : feature.avg,
            Min                    : feature.min,
            'Scaled [log(x+1)*10]' : feature.logx10,
            Score                  : feature.score
        };

        /*
                Synonyms : gene.synonyms,
                OMIM     : gene.id_omim ? '<a target=_blank href="http://omim.org/'+ gene.id_omim +'">'+ gene.id_omim +'</a>' : '-',
                Morbid   : gene.id_morbid ? '<a target=_blank href="http://omim.org/'+ gene.id_morbid +'">'+ gene.id_morbid +'</a>' : '-',
                'UCSC ID': gene.id_ucsc ? '<a target=_blank href="http://genome.cse.ucsc.edu/cgi-bin/hgGene?hgg_gene='+ gene.id_ucsc +'">'+ gene.id_ucsc +'</a>' : '-',
                Ensembl  : gene.id_ensembl ? '<a target=_blank href="http://www.ensembl.org/Homo_sapiens/Gene/Summary?g='+ gene.id_ensembl +'">'+ gene.id_ensembl +'</a>' : '-',
                Protein  : gene.id_uniprot ? '<a target=_blank href="http://www.uniprot.org/uniprot/'+ gene.id_uniprot +'">'+ gene.id_uniprot +'</a>' : '-'
        */
    },

    printData : function (data) {
        if (!this.enableDataView) {
            return '';
        }

        var nfeatures = data.length;
        var str0 = '';

        for (var i=0;i<nfeatures;i++) {
            str0 += data[i].item + "\t" +
                data[i].chr + ':' +  data[i].start + '-' + data[i].end + "\t" +
                data[i].min + "\t" +
                data[i].avg + "\t" +
                data[i].max + "\t" +
                data[i].score + "\t" +
                data[i].logx10 + "\n";
        }

        if (str0.length > 0) {
            str0 = this.name + ":\n" +
                "Id\tPosition\tScore_min\tScore_avg\tScore_max\tScore\tLog10(Score + 1)*10\n" +
                str0 + "\n";
        }
        return str0;
    }

});