Genoverse.Track.DAS.Transcript.Geuvadis = Genoverse.Track.DAS.Transcript.extend({

    populateExons : function (exons) {
        var s0 = "<table class=\"gv_in_menu_table\"><tr><td>Type</td><td>Position</td><td>Method</td><td>Ori</td><td>Width</td></tr>";
        var i = 0;
        while(i < exons.length) {
            s0 += "<tr>" +
                "<td>" + exons[i].type + "</td>" +
                "<td>" + exons[i].start + "-" + exons[i].end + "</td>" +
                "<td>" + exons[i].method + "</td>" +
                "<td>" + exons[i].orientation + "</td>" +
                "<td>" + exons[i].width + "</td>" + "</tr>";
            i++;
        }
        s0 = s0 + "</table>";
        return s0;
    },

    populateMenu : function (feature) {
        return {
            Transcript  : '<a target=_blank href="http://www.ensembl.org/Homo_sapiens/Transcript/Summary?t=' +
                               feature.id + ';db=core">'+ feature.id +'</a>',
            Location    : feature.start + '-' + feature.end, // feature.chr + ':' +
            Width       : feature.width,
            Type        : feature.type,
            Exons       : feature.exons ? feature.exons.length : '0',
            ' '         : feature.exons ? this.populateExons(feature.exons) : ' '
        };
    },

    printData : function (data) {
        var nfeatures = data.length;
        var str0 = '';

        for (var i=0;i<nfeatures;i++) {
            var nexons = data[i].exons.length;

            for (var j=0;j<nexons;j++) {
                str0 += data[i].id + "\t" +
                    data[i].start + '-' + data[i].end + "\t" +
                    data[i].exons[j].type + "\t" +
                    data[i].exons[j].method + "\t" +
                    data[i].exons[j].start + '-' +  data[i].exons[j].end + "\t" +
                    data[i].exons[j].orientation + "\t" +
                    data[i].exons[j].width;
                str0 += "\n";
            }
        }

        if (str0.length > 0) {
            str0 = this.name + ":\n" +
                "Transcript_id\tTranscript_position\tExon_type\tExon_method\tExon_position\tExon_orientation\tExon_width\n" +
                str0 + "\n";
        }

        return str0;
    }


});

