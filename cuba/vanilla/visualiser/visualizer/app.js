const express = require('express');
const app = express();
const path = require('path');
const router = express.Router();

router.get('/', function (req, res) {
  res.sendFile(path.join(__dirname + '/result.html'));
  //__dirname : It will resolve to your project folder.
});

router.get('/utilitarian', function (req, res) {
  res.sendFile(path.join(__dirname + '/utilitarian.html'));
  //__dirname : It will resolve to your project folder.
});

router.get('/effectiveutilitarian', function (req, res) {
  res.sendFile(path.join(__dirname + '/effectiveutilitarian.html'));
  //__dirname : It will resolve to your project folder.
});

router.get('/nonutilitarian', function (req, res) {
  res.sendFile(path.join(__dirname + '/nonutilitarian.html'));
  //__dirname : It will resolve to your project folder.
});

router.get('/utilitariannoncumul', function (req, res) {
  res.sendFile(path.join(__dirname + '/utilitariannoncumul.html'));
  //__dirname : It will resolve to your project folder.
});

router.get('/effectiveutilitariannoncumul', function (req, res) {
  res.sendFile(path.join(__dirname + '/effectiveutilitariannoncumul.html'));
  //__dirname : It will resolve to your project folder.
});

router.get('/nonutilitariannoncumul', function (req, res) {
  res.sendFile(path.join(__dirname + '/nonutilitariannoncumul.html'));
  //__dirname : It will resolve to your project folder.
});

/* router.get('/about',function(req,res){
  res.sendFile(path.join(__dirname+'/about.html'));
});

router.get('/sitemap',function(req,res){
  res.sendFile(path.join(__dirname+'/sitemap.html'));
}); */

//add the router
app.use('/', router);
//Resources
app.use(express.static(__dirname + '/images'));
app.use(express.static(__dirname + '/chart'));
// All the Results
app.use(express.static(path.join(__dirname + '/results')));
app.listen(process.env.port || 3000);

console.log('Running at Port 3000');