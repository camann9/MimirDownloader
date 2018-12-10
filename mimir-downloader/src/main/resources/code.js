$(document).on("click",".hideLink", function () {
   $(this).parent().next().toggleClass("hidden");
});

$(document).on("click",".examModeToggle", function () {
   $("body").toggleClass("examMode");
});