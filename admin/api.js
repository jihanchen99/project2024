var express = require('express');
var app = express();
app.use(express.json());

const {Organization} = require('./DbConfig.js');
const {Fund} = require('./DbConfig.js');
const {Contributor} = require('./DbConfig.js');
const {Donation} = require('./DbConfig.js');


/*
Return an org with login specified as req.query.login and password specified as 
req.query.password; this essentially acts as login for organizations
*/
app.use('/findOrgByLoginAndPassword', (req, res) => {

	var query = {"login" : req.query.login, "password" : req.query.password };
    
	Organization.findOne( query, (err, result) => {
		if (err) {
		    res.json({ "status": "error", "data" : err});
		}
		else if (!result){
		    res.json({ "status": "login failed" });
		}
		else {
		    //console.log(result);
		    res.json({ "status" : "success", "data" : result});
		}
	    });
    });

/*
Create a new fund
*/
app.use('/createFund', (req, res) => {

	var fund = new Fund({
		name: req.query.name,
		description: req.query.description,
		target: req.query.target,
		org: req.query.orgId,
		donations: []
	    });

	fund.save( (err) => {
		if (err) {
		    res.json({ "status": "error", "data" : err});
		}
		else {
		    //console.log(fund);

		    /*
		      In addition to updating the Fund collection, we also need to
		      update the Organization object to which this Fund belongs.
		    */

		    var filter = {"_id" : req.query.orgId };

		    var action = { "$push" : { "funds" : fund } };

		    Organization.findOneAndUpdate( filter, action, { "new" : true },  (err, result) => {
			    //console.log(result);
			    if (err) {
				res.json({ "status": "error", "data" : err});
			    }
			    else {
				res.json({ "status": "success", "data" : fund});
			    }
			});
		
		}
	    });

    });

/*
Return the Fund with ID specified as req.query.id
*/
app.use('/findFundById', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOne( query, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else {
		    //console.log(result);
		    res.json({'status' : 'success', 'data' : result});
		}
	    });
	
    });

/*
Delete the fund with ID specified as req.query.id
*/
app.use('/deleteFund', (req, res) => {

	var query = {"_id" : req.query.id };
    
	Fund.findOneAndDelete( query, (err, orig) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else {
		    //console.log(orig);

		    /*
		      In addition to removing this from the Fund collection,
		      we also need to remove it from the Organization to which it belonged.
		    */

		    var filter = {"_id" : orig.org };
		    var action = { "$pull" : { "funds" : { "_id" : req.query.id } } };

		    Organization.findOneAndUpdate(filter, action, (err) => {
			    if (err) {
				res.json({'status': 'error', 'data' : err});
			    }
			    else {
				res.json({'status': 'success', 'data': orig});
			    }
			});
		}
	    });

    });



/*
Return the name of the contributor with ID specified as req.query.id
*/
app.use('/findContributorNameById', (req, res) => {
    
	var query = { "_id" : req.query.id };
	
	Contributor.findOne(query, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else if (!result) {
		    res.json({'status': 'not found'});
		}
		else {
		    res.json({'status': 'success', 'data': result.name});
		}
		
	    });
    });


/*
Make a new donation to the fund with ID specified as req.query.fund
from the contributor with ID specified as req.query.contributor
*/
app.use('/makeDonation', (req, res) => {

	var donation = new Donation({
		contributor: req.query.contributor,
		fund: req.query.fund,
		date: Date.now(),
		amount: req.query.amount
	    });


	donation.save( (err) => {
		if (err) {
		    res.json({'status' : 'error', 'data' : err});
		}
		else {
		    //console.log(donation);

		    /*
		      In addition to creating a new Donation object, we need to update
		      the Contributor's array of donations
		    */

		    var filter = { "_id" : req.query.contributor };
		    var action = { "$push" : { "donations" : donation } };

		    Contributor.findOneAndUpdate(filter, action, (err) => {

			    if (err) {
				res.json({'status' : 'error', 'data' : err});
			    }
			    else {
				
				var filter = { "_id" : req.query.fund };
				var action = { "$push" : { "donations" : donation } };

				/*
				  We also need to update the Fund's array of donations
				*/
				Fund.findOneAndUpdate(filter, action, { "new" : true }, (err, fund) => {

					if (err) {
					    res.json({'status' : 'error', 'data' : err});
					}
					else {

					    var query = { "_id" : fund.org };

					    var whichFund = { "funds" : { "_id" : { "$in" : [fund._id] }  } }; 

					    var action = { "$pull" : whichFund };

					    /*
					      We also need to update the Fund in the Organization.
					      To do this, I remove the Fund and then replace it with the updated one.
					    */

					    Organization.findOneAndUpdate(query, action, (err, org) => {


						    if (err) {
							res.json({'status' : 'error', 'data' : err});
						    }
						    else {    

							var query = { "_id" : fund.org };
					    
							var action = { "$push" : { "funds" : fund } };
							
							Organization.findOneAndUpdate(query, action, { "new" : true }, (err, org) => {
								
								if (err) {
								    res.json({'status' : 'error', 'data' : err});
								}
								else {
								    //console.log(org);
								    res.json({'status' : 'success', 'data' : donation });
								}


							    });


						    }

						});

					}
				    });
			    }


			});

		}
	    });

    });


    

/*
Return the contributor with login specified as req.query.login and password specified
as req.query.password; essentially acts as login for contributors.
*/
app.use('/findContributorByLoginAndPassword', (req, res) => {

	var query = {"login" : req.query.login, "password" : req.query.password };
    
	Contributor.findOne( query, (err, result) => {
		if (err) {
		    res.json({ "status": "error", "data" : err});
		}
		else if (!result){
		    res.json({ "status": "login failed" });
		}
		else {
		    //console.log(result);
		    res.json({ "status" : "success", "data" : result});
		}
	    });
	

    });


/*
Return the name of the fund with ID specified as req.query.id
*/
app.use('/findFundNameById', (req, res) => {
    
	var query = { "_id" : req.query.id };
	
	Fund.findOne(query, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else if (!result) {
		    res.json({'status': 'not found'});
		}
		else {
		    res.json({'status': 'success', 'data': result.name});
		}
		
	    });
    });


//3.3

app.post('/updateOrgInfo', (req, res) => {
    const { orgId, name, description } = req.body;
    console.log("Received orgId:", orgId);
	const orgId2 = "6664be4735256e45e4d4df29"; // Replace with a valid orgId from your database
	
    console.log("Received update request:", req.body); // Debugging line
    console.log("Parsed values:", { orgId2, name, description }); // Additional debugging line

    var filter = { "_id": orgId };
    var update = { "name": name, "description": description };
    var action = { "$set": update };

    Organization.findOneAndUpdate(filter, action, { new: true }, (err, result) => {
        if (err) {
            console.error("Error finding/updating organization:", err); // Debugging line
            return res.status(500).json({ "status": "error", "data": err });
        }
        if (!result) {
            console.error("Organization not found for ID:", orgId); // Debugging line
            return res.status(404).json({ "status": "not found", "message": "Organization not found" });
        }
        console.log("Organization updated successfully:", result); // Debugging line
        res.json({ "status": "success", "data": result });
    });
});

/*
app.post('/updateOrgInfo', (req, res) => {
    // Hard-code the orgId for debugging purposes
    const orgId = "6664be4735256e45e4d4df29"; // Replace with a valid orgId from your database
    const {name, description } = req.body;
    
    console.log("Received update request:", { orgId, name, description }); // Debugging line

    var filter = { "_id": orgId };
    var update = { "name": name, "description": description };
    var action = { "$set": update };

    Organization.findOneAndUpdate(filter, action, { new: true }, (err, result) => {
        if (err) {
            console.error("Error finding/updating organization:", err); // Debugging line
            return res.status(500).json({ "status": "error", "data": err });
        }
        if (!result) {
            console.error("Organization not found for ID:", orgId); // Debugging line
            return res.status(404).json({ "status": "not found", "message": "Organization not found" });
        }
        console.log("Organization updated successfully:", result); // Debugging line
        res.json({ "status": "success", "data": result });
    });
});
*/

/*

app.post('/updateOrgInfo', (req, res) => {
    const { orgId, name, description } = req.body;

    // Define the update action
    var update = { name, description };

    // Attempt to find the organization and update it
    Organization.findByIdAndUpdate(orgId, update, { new: true }, (err, org) => {
        if (err) {
            // If there is an error during the update, return an error response
            res.status(500).json({ message: "Error updating organization", error: err });
        } else if (!org) {
            // If no organization was found, return a not found response
            res.status(404).json({ message: "Organization not found" });
        } else {
            // If the update is successful, return a success response with the updated organization
            res.json({ message: "Organization updated successfully", org });
        }
    });
});


*/

/*
Return information about all organizations, so that user can choose one to contribute to.
Rather than return all info in database, this just returns the org's ID, name, and funds.
For each fund, it only includes the ID, name, target, and total donations so far.
*/
app.use('/allOrgs', (req, res) => {

	Organization.find({}, (err, result) => {
		if (err) {
		    res.json({'status': 'error', 'data' : err});
		}
		else {

		    var organizations = [];

		    result.forEach( (org) => {
			    
			    var funds = [];

			    org.funds.forEach( (fund) => {

				    var totalDonations = 0;

				    fund.donations.forEach( (donation) => {
					    totalDonations += donation.amount;
					});

				    var fundResult = {
					'_id' : fund._id,
					'name' : fund.name,
					'target' : fund.target,
					'totalDonations' : totalDonations
				    };

				    funds.push(fundResult);

				});

			    var orgResult = {
				'_id' : org._id,
				'name' : org.name,
				'funds' : funds
			    };

			    organizations.push(orgResult);

			});

		    //console.log(organizations);
		    res.json({'status' : 'success', 'data': organizations});
		}
	    }).sort({ 'name': 'asc' });
    });



app.get('/updateOrgInfo', (req, res) => {
    const { orgId, name, description } = req.query; 

    console.log("Received orgId:", orgId);
    console.log("Received update request:", { orgId, name, description });

    var filter = { "_id": orgId };
    var update = { "name": name, "description": description };
    var action = { "$set": update };

    console.log("Filter object:", filter);
    console.log("Update object:", update);

    Organization.findOneAndUpdate(filter, action, { new: true }, (err, result) => {
        if (err) {
            console.error("Error finding/updating organization:", err); 
            return res.status(500).json({ "status": "error", "data": err });
        }
        if (!result) {
            console.error("Organization not found for ID:", orgId); 
            return res.status(404).json({ "status": "not found", "message": "Organization not found" });
        }
        console.log("Organization updated successfully:", result);
        res.json({ "status": "success", "data": result });
    });
});

//3.3
app.use('/findOrgLoginById', (req, res) => {
    var query = { "_id": req.query.orgId };

    Organization.findOne(query, (err, result) => {
        if (err) {
            res.json({ "status": "error", "data": err });
        } else if (!result) {
            res.json({ "status": "not found" });
        } else {
            res.json({ "status": "success", "data": result.login });
        }
    });
});

// Task 3.2
/*
Handle the form submission to change the organization's password
*/
app.use('/changePassword', (req, res) => {
	const requestData = { ...req.body, ...req.query };

	var filter = { "_id": requestData.orgId, "password": requestData.currentPassword };

	var update = { "password": requestData.newPassword };
	var action = { "$set": update };

	Organization.findOneAndUpdate(filter, action, { new: true }, (err, result) => {
		if (err) {
			res.status(500).json({ "status": "error", "data": err });
		} else if (!result) {
			res.status(400).json({ "status": "error", "message": "Current password is incorrect" });
		} else {
			res.json({ "status": "success", "message": "Password changed successfully" });
		}
	});
});

/********************************************************/


app.listen(3001,  () => {
	console.log('Listening on port 3001');
    });
