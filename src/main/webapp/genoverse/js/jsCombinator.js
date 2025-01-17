// Copied from web.student.tuwien.ac.at/~e0427417/jsdownload.html

var showSave;

// Feature test: Does this browser support the download attribute on anchor tags? (currently only Chrome)
var DownloadAttributeSupport = 'download' in document.createElement('a');

// Use any available BlobBuilder/URL implementation:
var BlobBuilder = window.BlobBuilder || window.WebKitBlobBuilder || window.MozBlobBuilder || window.MSBlobBuilder;
var URL = window.URL || window.webkitURL || window.mozURL || window.msURL;

// IE 10 has a handy navigator.msSaveBlob method. Maybe other browsers will emulate that interface?
// See: http://msdn.microsoft.com/en-us/library/windows/apps/hh441122.aspx
navigator.saveBlob = navigator.saveBlob || navigator.msSaveBlob || navigator.mozSaveBlob || navigator.webkitSaveBlob;

// Anyway, HMTL5 defines a very similar but more powerful window.saveAs function:
// http://www.w3.org/TR/file-writer-api/#the-filesaver-interface
window.saveAs = window.saveAs || window.webkitSaveAs || window.mozSaveAs || window.msSaveAs;
// However, this is not supported by any browser yet. But there is a compatibility library that
// adds this function to browsers that support Blobs (except Internet Exlorer):
// http://eligrey.com/blog/post/saving-generated-files-on-the-client-side
// https://github.com/eligrey/FileSaver.js

// mime types that (potentially) don't trigger a download when opened in a browser:
var BrowserSupportedMimeTypes = {
  "image/jpeg": true,
  "image/png": true,
  "image/gif": true,
  "image/svg+xml": true,
  "image/bmp": true,
  "image/x-windows-bmp": true,
  "image/webp": true,
  "audio/wav": true,
  "audio/mpeg": true,
  "audio/webm": true,
  "audio/ogg": true,
  "video/mpeg": true,
  "video/webm": true,
  "video/ogg": true,
  "text/plain": true,
  "text/html": true,
  "text/xml": true,
  "application/xhtml+xml": true,
  "application/json": true
};

// Blobs and saveAs (or saveBlob) :
if (BlobBuilder && (window.saveAs || navigator.saveBlob)) {
  // Currently only IE 10 supports this, but I hope other browsers will also implement the saveAs/saveBlob method eventually.
  showSave = function (data, name, mimeType) {
    var builder = new BlobBuilder();
    builder.append(data);
    var blob = builder.getBlob(mimetype||"application/octet-stream");
    if (!name) name = "Download.bin";
    // I don't assign saveAs to navigator.saveBlob (or the other way around)
    // because I cannot know at this point whether future implementations
    // require these methods to be called with 'this' assigned to window (or
    // naviagator) in order to work. E.g. console.log won't work when not called
    // with this === console.
    if (window.saveAs) {
      window.saveAs(blob, name);
    }
    else {
      navigator.saveBlob(blob, name);
    }
  };
}
// Blobs and object URLs:
else if (BlobBuilder && URL) {
  // Currently WebKit and Gecko support BlobBuilder and object URLs.
  showSave = function (data, name, mimetype) {
    var blob, url, builder = new BlobBuilder();
    builder.append(data);
    if (!mimetype) mimetype = "application/octet-stream";
    if (DownloadAttributeSupport) {
      blob = builder.getBlob(mimetype);
      url = URL.createObjectURL(blob);
      // Currently only Chrome (since 14-dot-something) supports the download attribute for anchor elements.
      var link = document.createElement("a");
      link.setAttribute("href",url);
      link.setAttribute("download",name||"Download.bin");
      // Now I need to simulate a click on the link.
      // IE 10 has the better msSaveBlob method and older IE versions do not support the BlobBuilder interface
      // and object URLs, so we don't need the MS way to build an event object here.
      var event = document.createEvent('MouseEvents');
      event.initMouseEvent('click', true, true, window, 1, 0, 0, 0, 0, false, false, false, false, 0, null);
      link.dispatchEvent(event);
    }
    else {
      // In other browsers I open a new window with the object URL.
      // In order to trigger a download I have to use the generic binary data mime type
      // "application/octet-stream" for mime types that browsers would display otherwise.
      // Of course the browser won't show a nice file name here.
      if (BrowserSupportedMimeTypes[mimetype.split(";")[0]] === true) {
        mimetype = "application/octet-stream";
      }

      blob = builder.getBlob(mimetype);
      url = URL.createObjectURL(blob);
      window.open(url, '_blank', '');
    }
    // The timeout is probably not necessary, but just in case that some browser handle the click/window.open
    // asynchronously I don't revoke the object URL immediately.
    setTimeout(function () {
      URL.revokeObjectURL(url);
    }, 250);

    // Using the filesystem API (http://www.w3.org/TR/file-system-api/) you could do something very similar.
    // However, I think this is only supported by Chrome right now and it is much more complicated than this
    // solution. And chrome supports the download attribute anyway.
  };
}
// data:-URLs:
else if (!/\bMSIE\b/.test(navigator.userAgent)) {
  // IE does not support URLs longer than 2048 characters (actually bytes), so it is useless for data:-URLs.
  // Also it seems not to support window.open in combination with data:-URLs at all.
  showSave = function (data, name, mimetype) {
    if (!mimetype) mimetype = "application/octet-stream";
    // Again I need to filter the mime type so a download is forced.
    if (BrowserSupportedMimeTypes[mimetype.split(";")[0]] === true) {
      mimetype = "application/octet-stream";
    }
    // Note that encodeURIComponent produces UTF-8 encoded text. The mime type should contain
    // the charset=UTF-8 parameter. In case you don't want the data to be encoded as UTF-8
    // you could use escape(data) instead.
    window.open("data:"+mimetype+","+encodeURIComponent(data), '_blank', '');
  };
}
// Internet Explorer before version 10 does not support any of the methods above.
// If it is text data you could show it in an textarea and tell the user to copy it into a text file.



$.when.apply($, 
  $.map($('script:not(:last)[src]'), function (script) { 
    return $.ajax({
      url : $(script).attr('src'),
      dataType : "text",
    });
  })
).done(function () {
  var src = "(function () {\n";

  for (var i=0; i<arguments.length; i++) {
    src = src + arguments[i][0] + "\n\n\n\n\n";
  }

  src = src + "                                    \n\
    var thisScriptTag = $('script:last');          \n\
    var config = thisScriptTag.text();             \n\
    if (config) {                                  \n\
      try {                                        \n\
        config = eval('('+ config +')');           \n\
        $(document).ready(function(){              \n\
          window.genoverse = new Genoverse(config) \n\
        });                                        \n\
      } catch (e) {                                \n\
        throw('Configuration ERROR:' + e);         \n\
      };                                           \n\
    }                                              \n\
    })();                                          \n\
    $.noConflict(true);";

  showSave(src, 'combined.js', 'text/plain; charset=UTF-8');
});