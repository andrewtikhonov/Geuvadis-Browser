Genoverse.Track.GeuvadisEnsemblGeneTrack = Genoverse.Track.extend({

    name           : "Genes Ensembl",
    url            : "gene?r=__CHR__:__START__-__END__",
    height         : 100,
    featureHeight  : 6,
    featureSpacing : 2,
    bump           : true,
    labels         : true,
    resizable      : true,
    featureNormal  : "rgba(250,0,0,0.8)",
    featureDark    : "rgba(100,0,0,0.8)",
    info           : "Genes Ensembl",

    setFeatureColor: function (feature) {
      var color = '#000000';

      if (feature.logic_name.indexOf('ensembl_havana') === 0) {
        color = '#cd9b1d';
      } else if (feature.biotype.indexOf('RNA') > -1) {
        color = '#8b668b';
      } else switch (feature.biotype) {
        case 'protein_coding'       : color = '#A00000'; break;
        case 'processed_transcript' : color = '#0000FF'; break;
        case 'antisense'            : color = '#0000FF'; break;
        case 'sense_intronic'       : color = '#0000FF'; break;
        case 'pseudogene'           :
        case 'processed_pseudogene' : color = '#666666'; break;
        default                     : color = '#A00000'; break;
      }

      feature.color = feature.labelColor = color;
    },

    parseData      : function (data) {
        var i = data.length;
        while (i--) {
            data[i].label      = data[i].name;
            data[i].color      = this.featureNormal;
            data[i].labelColor = this.featureNormal;

            if (data[i].biotype == "protein_coding" || data[i].biotype == "lincRNA") {
                data[i].color      = this.featureDark;
                data[i].labelColor = this.featureDark;
            }

            data[i].start      = parseInt(data[i].start, 10);
            data[i].end        = parseInt(data[i].end, 10);
            this.insertFeature(data[i]);
        }
    },

    packDescription      : function (desc) {
        if (desc.length > 38) {
            var i0 = desc.indexOf(" ", 38);

            if (i0 != -1) {
                desc = desc.substring(0, i0) + "</br>" + desc.substring(i0);
            }

            return desc;
        }
    },

    populateMenu : function (gene) {
        return {
            Gene         : '<a target=_blank href="http://www.genenames.org/data/hgnc_data.php?match='+ gene.name+ '">' + gene.name + '</a>',
            Description  : gene.description ? this.packDescription( gene.description ) : '-' ,
            Location     : gene.chr + ':' + gene.start + '-' + gene.end,
            Biotype      : gene.biotype,
            Ensembl      : gene.id ? '<a target=_blank href="http://www.ensembl.org/Homo_sapiens/Gene/Summary?g='+ gene.id +'">'+ gene.id +'</a>' : '-'
        };
    },

    printData : function (data) {
        var nitems = data.length;
        var str0 = '';

        for (var i = 0;i < nitems;i++) {
            str0 += data[i].name + "\t" +
                data[i].id + "\t" +
                data[i].biotype + "\t" +
                data[i].chr + ':' +  data[i].start + '-' + data[i].end;
            str0 += "\n";
        }

        if (str0.length > 0) {
            str0 = this.name  + ":\n" +
                "Gene_Name\tEnsembl_id\tBiotype\tLocation\n" +
                str0 + "\n";
        }

        return str0;
    }

});

