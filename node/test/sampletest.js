var expect = require('expect.js'),
	assert = require('assert')
	Browser = require('zombie'),
	browser = new Browser();
Browser.localhost('localhost', 3000);

describe('Loads pages', function(){
    it('Heatmaps', function(done){
        browser.visit('/', function () {
        	this.timeout(15000);
    		setTimeout(done, 15000);
            expect(browser.text("title")).to.equal('Heatmapss');
            done();
        });
    });
});

describe('Loads pages', function(){
    it('Heatmaps chart', function(done){
        browser.visit('/chart', function () {
            expect(browser.text("title")).to.equal('Heatmaps');
            done();
        });
    });
});


describe('contact page', function() {

  it('should show contact a form', function() {
    assert.ok(this.browser.success);
    assert.equal(this.browser.text('h1'), 'Contact');
    assert.equal(this.browser.text('form label'), 'First NameLast NameEmailMessage');
  });

});