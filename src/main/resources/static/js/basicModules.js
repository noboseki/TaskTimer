$.get("/basicHTML/head", function(data) {
        $("head").append(data);
    });

$.get("/basicHTML/header", function(data) {
        $("header").append(data);
    });

$.get("/basicHTML/navigation", function(data) {
        $("nav").append(data);
    });

$.get("/basicHTML/footer", function(data) {
        $("footer").append(data);
    });