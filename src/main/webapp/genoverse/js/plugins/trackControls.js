var defaultControls = [
  $('<a title="More info">').html('<i class="icon-info"></i>').on('click', function (e) {
      var track  = $(this).data('track');

      if (!track.menus.filter('.track_info').length) {
        track.browser.makeMenu({
          title : track.name,
          ' '   : track.info
        }, { top  :e.pageY - 50 } ).addClass('track_info'); // .css(offset)
      }
  }),

  $('<a title="Close track">').html('<i class="icon-remove"></i>').on('click', function () {
    var track = $(this).data('track');
    track.remove();
  })
];

var toggle = $('<a>').html('&raquo;').on('click', function () {
  if ($(this).parent().hasClass('maximized')) {
    $(this)
      .parent().removeClass('maximized').end()
      .siblings().css({ display: 'none' }).end()
      .html('&raquo;');
  } else {
    $(this)
      .parent().addClass('maximized').end()
      .siblings().css({ display: 'inline-block' }).end()
      .html('&laquo;');
  }
});

Genoverse.Track.on('afterAddDomElements', function() {
  if (this.controls === 'off') {
    return;
  }

  var controls = (this.controls || []).concat(defaultControls);

  this.trackControls = $('<div class="track_controls static">').prependTo(this.container); // prependTo // container // label

  toggle.clone(true).data('track', this).appendTo(this.trackControls);

  for (var i = 0; i < controls.length; i++) {
    controls[i].clone(true).css({ display: 'none' }).data('track', this).appendTo(this.trackControls);
  }

});

Genoverse.Track.on('afterResize', function() {
  if (this.trackControls) {
    this.trackControls[this.height < this.trackControls.outerHeight(true) ? 'hide' : 'show']();
  }
});

