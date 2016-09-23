var expect = require('expect.js'),
	assert = require('assert')
	Browser = require('zombie'),
	browser = new Browser();
Browser.localhost('localhost', 3000);
/*
describe('Heatmaps page', function(){
	it("should have defined headless browser", function(next){
        expect(typeof browser != "undefined").to.be(true);
        expect(browser instanceof Browser).to.be(true);
        next();
    });

    it('should have header navigation bar', function(next){
        browser.visit('/chart', function () {
        	expect(browser.html()).to.exist;
            expect(browser.text("title")).to.equal('Heatmaps');
            expect(browser.query("div#wrapper")).to.exist;
            expect(browser.query("div.container")).to.exist;
            expect(browser.query("nav")).to.exist;
            next();
        });
    });

    it('should include the google apis', function(done){
    	this.timeout(5500);
    	//setTimeout(done, 55000);
        browser.visit('/', function () {
            expect(browser.text("title")).to.equal('Heatmaps');
            expect(browser.query("div#map")).to.exist;
            expect(browser.query("script")).to.exist;
            var browserScripts = browser.query("script");
            done();
        });
    });

});
*/
describe('Heatmaps page', function(){
	it("should have defined headless browser", function(next){
        expect(typeof browser != "undefined").to.be(true);
        expect(browser instanceof Browser).to.be(true);
        next();
    });

    it('should include the google apis', function(done){
    	this.timeout(5500);
    	//setTimeout(done, 55000);
        browser.visit('/', function () {
            expect(browser.text("title")).to.equal('Heatmaps');
            expect(browser.query("div#map")).to.exist;
            expect(browser.query("script")).to.exist;
            var browserScripts = browser.query("script");
            done();
        });
    });
});

describe('Chart page', function(){

	it("should have defined headless browser", function(next){
        expect(typeof browser != "undefined").to.be(true);
        expect(browser instanceof Browser).to.be(true);
        next();
    });

    it('should have header navigation bar', function(next){
        browser.visit('/chart', function () {
        	expect(browser.html()).to.exist;
            expect(browser.text("title")).to.equal('Heatmaps');
            expect(browser.query("div#wrapper")).to.exist;
            expect(browser.query("div.container")).to.exist;
            expect(browser.query("nav")).to.exist;
            next();
        });
    });

    it('should show the chart', function(next){
        browser.visit('/chart', function () {
            expect(browser.query("div.highcharts-container")).to.exist;
            expect(browser.query("g.highcharts-series-group")).to.exist;
            expect(browser.query("g.highcharts-series")).to.exist;
            expect(browser.query("g.highcharts-button")).to.exist;
            next();
        });
    });

    it('the print event funtion should work', function(next){
        browser.visit('/chart', function () {
            expect(browser.query("g.highcharts-button")).to.exist;
            browser.evaluate("$('g.highcharts-button').click()"); 
            next();
        });
    });
});


describe('Login page', function() {

    it("should have defined headless browser", function(next){
        expect(typeof browser != "undefined").to.be(true);
        expect(browser instanceof Browser).to.be(true);
        next();
    });

    it("should visit the site and see the login form", function(next) {
        browser.visit('/login', function(err) {
            expect(browser.success).to.be(true);
            expect(browser.query("input[name='email']")).to.exist;
            expect(browser.query("input[name='password']")).to.exist;
            next();
        })
    });

    it("fill in and check if form works", function(next) {
        browser.visit('/login', function(err) {
            expect(browser.success).to.be(true);
            browser.fill("input[name='email']", 'test@email.com');
            browser.fill("input[name='password']", 'parola');
            expect(browser.query("button.btn-login")).to.exist;
            browser.document.forms[0].submit();
		    browser.wait().then(function() {
		        console.log(browser.dump());
		    })
            next();
        })
    });

    it('should show contact a form', function() {
		browser.visit('/login', function (done) {
		    assert.ok(this.browser.success);
		    assert.equal(this.browser.text('h1'), 'Contact');
		    assert.equal(this.browser.text('form label'), 'First NameLast NameEmailMessage');
		    done();
		});
	});

    it('email and password inputs', function() {
		browser.visit('/login', function (done) {
		    assert.ok(this.browser.success);
		    assert.equal(this.browser.text('h1'), 'Contact');
		    assert.equal(this.browser.text('form label'), 'First NameLast NameEmailMessage');
		    next();
		});
    });

});