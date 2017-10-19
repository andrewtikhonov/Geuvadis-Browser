Genoverse.Track.Model.QuantJsonModel = Genoverse.Track.Model.extend({

    name             : 'Quantification Track',
    url              : 'exon?r=__CHR__:__START__-__END__',
    dataType         : 'json',
    featureColor     : 'rgba(200,0,0,0.8)',
    dataRequestLimit : 5000000, // As per e! REST API restrictions
    searchUrl        : 'www.google.com/search?q=',
    enableDataView   : true,

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
    },

    populateMenu : function (feature) {
        return {
            title                  :
                "<a target=_blank href=\"" + this.searchUrl +
                (this.name2Term != null ? (this.name2Term(feature.item)) : feature.item) + "\"> " +
                feature.item + "</a>",
            Location               : feature.chr + ':' + feature.start + '-' + feature.end,
            Max                    : feature.max,
            Avg                    : feature.avg,
            Min                    : feature.min,
            'Scaled [log(x+1)*10]' : feature.logx10,
            Score                  : feature.score
        };
    }

});