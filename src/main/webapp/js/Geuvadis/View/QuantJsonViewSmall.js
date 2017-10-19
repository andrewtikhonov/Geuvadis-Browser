Genoverse.Track.View.QuantJsonViewSmall = Genoverse.Track.View.extend({

    threshold      : 10000000,
    name           : 'Quantification View',
    height         : 100,
    featureHeight  : 98,
    labels         : 'overlay',
    resizable      : false,

    draw: function (features, featureContext, labelContext, scale) {
        //alert("Geuvadis draw features.length=" + features.length);
        for (var i=0; i<features.length; i += 3) {
            var feature = features[i];
            featureContext.fillStyle = feature.color;
            featureContext.fillRect(Math.round(feature.position[scale].X),
                    this.featureHeight - Math.round(feature.position[scale].Y),
                    feature.position[scale].width, -Math.round(feature.logx10));
        }
    }
});