db.setLogLevel(5);
db.adminCommand({
    createRole: "mongoDBAdmin",
    privileges: [
        {resource: {anyResource: true}, actions: ["anyAction"]}
    ],
    roles: [
        {role: "userAdminAnyDatabase", db: "admin"}
    ],
});

db = db.getSiblingDB("admin");
db.createUser({
    user: "admin",
    pwd: "admin",
    roles: ["mongoDBAdmin"]
});

db = db.getSiblingDB("pullrequest");
db.createUser({
    user: "pullrequest",
    pwd: "bluesoft8437",
    roles: [
        {role: "readWrite", db: "pullrequest"}
    ]
});
