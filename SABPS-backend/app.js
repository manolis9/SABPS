var express = require('express');
var app = express();
var firebase = require('firebase');
var nodemailer = require('nodemailer');
var PORT = process.env.PORT || 3000;
var bodyParser = require('body-parser');

// firebase.initializeApp({
// 	serviceAccount: "./app/SABPS-595760d743f6.json",
// 	databaseURL: "https://sabps-cd1b7.firebaseio.com"

// });

app.use(bodyParser.json());

app.get('/', function(req, res) {

	var config = {
		apiKey: "AIzaSyAq4VZqZ2yC5JLioLbdLRIJ2JPh0oW1r7w",
		authDomain: "sabps-cd1b7.firebaseapp.com",
		databaseURL: "https://sabps-cd1b7.firebaseio.com",
		storageBucket: "sabps-cd1b7.appspot.com",
		messagingSenderId: "881638063284"
	};
	firebase.initializeApp(config);

	var smtpConfig = {
		host: 'smtp.office365.com',
		port: 587,
		secure: false, // use SSL
		auth: {
			user: 'manolis.ioannides@mazdis.com',
			pass: 'Joannakrupa_9'
		}
	};

	var transporter = nodemailer.createTransport(smtpConfig);

	firebase.database().ref().child('Emails to Send').on('child_changed', function(emailSnap) {
		var email = emailSnap.val();
		//sendEmailHelper(email.from, email.to, email.subject, email.body);
		console.log(email.from);
		console.log(email.to);
		console.log(email.subject);
		console.log(email.body);

		var mailOptions = {
			from: email.from.toString(), // sender address
			to: email.to.toString(), // list of receivers
			subject: email.subject.toString(), // Subject line
			text: email.body.toString(), // plaintext body
			html: '<b>' + email.body.toString() + '</b>' // html body
		}

		transporter.sendMail(mailOptions, function(error, info) {
			if (error) {
				return console.log(error);
			}
			console.log('Message sent: ' + info.response);
		});

		// firebase.database().ref().child('Emails to send').off("child_changed");
		// Remove it now that we've processed it.
		// firebase.database().ref().child('Emails to send').child('email').remove();

	});

	res.send('MAZDIS - SABPS');

});

app.listen(PORT, function() {
	console.log('Express listening on port: ' + PORT + '!');
});