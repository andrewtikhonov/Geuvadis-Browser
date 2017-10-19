


var defaultControls = [
  {
    icon   : '<i class="icon-remove" alt="close"></i>', // '<img src="genoverse/i/cross2.png" alt="close">',
    name   : 'close',
    action : function () {
      this.hide();
    }
  },
  {
    icon   : '<i class="icon-info" alt="information"></i>', // '<img src="genoverse/i/information.png" alt="information">',
    name   : 'Information',
    action : function (e) {

      this.browser.makeMenu({
        title : this.name,
        ' ' : this.info
      }, { top  :e.pageY - 50 } ).addClass('track_info');
    }    
  }
];

//afterRename
Genoverse.Track.on('afterAddDomElements afterRename', function() {
  var track = this;
  if (track.controls === 'off') return;
  var controls = (track.controls || []).concat(defaultControls);

  //debugger;
  var $div = $('<div class="track_controls" />');
  var $set = $('<div class="track_button_set" />');

  for (var i=0; i<controls.length; i++) {
    (function(control){
      var $control = $('<button class="track_button"/>')
        .html(control.icon)
        .attr({title: control.name})
        .on('click', function (e) {
          control.action.apply(track, [e]);
        });

      $set.append($control);
    })(controls[i])
  }

  $div.append($set);

  this.label.children('span.name').append($div);
});