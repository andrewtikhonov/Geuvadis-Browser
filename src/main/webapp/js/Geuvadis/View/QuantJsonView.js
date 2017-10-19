Genoverse.Track.View.QuantJsonView = Genoverse.Track.View.extend({

    threshold      : 1000000,
    name           : 'Quantification View',
    height         : 100,
    featureHeight  : 98,
    labels         : 'overlay',
    resizable      : false,

    draw: function (features, featureContext, labelContext, scale) {
    //draw: function (features, context, scale) {
        //alert("Geuvadis draw features.length=" + features.length);
        for (var i=0; i<features.length; i++) {
            var feature = features[i];
            featureContext.fillStyle = feature.color;
            featureContext.fillRect(Math.round(feature.position[scale].X),
                    this.featureHeight - Math.round(feature.position[scale].Y),
                    feature.position[scale].width, -Math.round(feature.logx10));
        }
    }

    /*
    10000: {
        view: Genoverse.Track.View.extend({
            bump       : false,
            labels     : true,
            autoHeight : false,
            bumpSpacing    : 0,
            featureSpacing : 0,

            draw: function (features, featureContext, labelContext, scale) {
            //draw: function (features, context, scale) {
                //alert("Geuvadis draw features.length=" + features.length);
                for (var i=0; i<features.length; i += 3) {
                    var feature = features[i];
                    featureContext.fillStyle = feature.color;
                    featureContext.fillRect(Math.round(feature.position[scale].X),
                            this.featureHeight - Math.round(feature.position[scale].Y),
                            feature.position[scale].width, -Math.round(feature.logx10));
                }
            }
        })
    },

    1: {
        view: Genoverse.Track.View.extend({
            bump       : false,
            labels     : true,
            autoHeight : false,
            bumpSpacing    : 0,
            featureSpacing : 0,

            draw: function (features, featureContext, labelContext, scale) {
            //draw: function (features, context, scale) {
                //alert("Geuvadis draw features.length=" + features.length);
                for (var i=0; i<features.length; i++) {
                    var feature = features[i];
                    featureContext.fillStyle = feature.color;
                    featureContext.fillRect(Math.round(feature.position[scale].X),
                            this.featureHeight - Math.round(feature.position[scale].Y),
                            feature.position[scale].width, -Math.round(feature.logx10));
                }
            }
        })
    },
    */

    /*
    100000: {
        view: Genoverse.Track.View.extend({
            bump       : false,
            labels     : false,
            autoHeight : false,
            bumpSpacing    : 0,
            featureSpacing : 0,

            //bump           : true,
            //labels         : false,
            //bumpSpacing    : 0,
            //featureSpacing : 0,
            //autoHeight     : false,

            draw: function (features, context, scale) {
                //alert("Geuvadis draw features.length=" + features.length);

                // skip 2 features
                //
                for (var i=0; i<features.length; i+=3) {
                    var feature = features[i];
                    context.fillStyle = feature.color;

                    context.strokeStyle = 'black';

                    //context.strokeRect(features[i].position[scale].X, features[i].position[scale].Y, features[i].position[scale].width, features[i].position[scale].height);

                    context.fillRect(Math.round(feature.position[scale].X),
                            this.featureHeight - Math.round(feature.position[scale].Y),
                            feature.position[scale].width, -Math.round(feature.logx10));
                }
            }
        })
    }
    */

    /*
    10000000: {
      view: Genoverse.Track.View.extend({
        bump       : false,
        labels     : false,
        autoHeight : false,


        setFeatureColor: function (feature) {
          var QUAL = feature.originalFeature[5];

          if (QUAL > 0) {
            var heat  = Math.min(255, Math.floor(255 * QUAL / this.maxQUAL)) - 127;
            var red   = heat > 0 ? 255 : 127 + heat;
            var green = heat < 0 ? 255 : 127 - heat;

            feature.color = 'rgb(' + red + ',' + green + ',0)';
          } else {
            feature.color = 'rgb(0,0,0)';
          }
        }


      }*/

    /*
            draw: function (features, featureContext, labelContext, scale) {
          this.base.apply(this, arguments);
          this.highlightRef(features, featureContext, scale);
        },

        highlightRef: function (features, context, scale) {
          context.strokeStyle = 'black';

          for (var i = 0; i < features.length; i++) {
            if (features[i].allele === 'REF') {
              context.strokeRect(features[i].position[scale].X, features[i].position[scale].Y, features[i].position[scale].width, features[i].position[scale].height);
            }
          }
        }
    */
    /*
    1000: {
      view: Genoverse.Track.View.extend({
        bump       : false,
        labels     : false,
        autoHeight : false,

        setFeatureColor: function (feature) {
          var QUAL = feature.originalFeature[5];

          if (QUAL > 0) {
            var heat  = Math.min(255, Math.floor(255 * QUAL / this.maxQUAL)) - 127;
            var red   = heat > 0 ? 255 : 127 + heat;
            var green = heat < 0 ? 255 : 127 - heat;

            feature.color = 'rgb(' + red + ',' + green + ',0)';
          } else {
            feature.color = 'rgb(0,0,0)';
          }
        }
      })
    }
        */

    /*
    populateMenu: function (feature) {
      return {
        'title'         : 'This is the title',
        'Location'      : feature.originalFeature[0] + ':' + feature.start + '-' + feature.end,
        'Ref allele'    : feature.originalFeature[3],
        'Alt allele(s)' : feature.originalFeature[4],
        'Qual'          : feature.originalFeature[5]
      };
    },
    */

});