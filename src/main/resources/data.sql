MERGE INTO users
(user_id, email, username, password, firstname, lastname, enabled) VALUES
('2','admin@gmail.com','admin','$2a$10$UN.AKAyrZPSzUAvHYJeyduasXjZqEodcEhdSM2Y0uwQx.USuOigI.','adminFirstName','adminLastName',true),
('3','teamLead@gmail.com','teamLead','$2a$10$PKjU7ozoNNnuV6FwnFYFe.VDkP0zheKB1ct2/af95L54eJ1q11pfy','teamLeadFirstName','teamLeadLastName',true),
('4','teamMember@gmail.com','teamMember','$2a$10$wcp6MGIzsA.4NXxw2O2dJeRYx1Uw5zgeAntZHubeENouWaeaFANKW','teamMemberFirstName','teamMemberLastName',true);

MERGE INTO user_authorities
(username, authorities) VALUES
('teamLead','ROLE_TEAM_LEAD'),
('teamMember','ROLE_TEAM_MEMBER'),
('admin','ROLE_ADMIN');

