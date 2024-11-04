INSERT INTO tool (code, type, brand) VALUES ('CHNS', 'Chainsaw', 'Stihl');
INSERT INTO tool (code, type, brand) VALUES ('LADW', 'Ladder', 'Werner');
INSERT INTO tool (code, type, brand) VALUES ('JAKD', 'Jackhammer', 'DeWalt');
INSERT INTO tool (code, type, brand) VALUES ('JAKR', 'Jackhammer', 'Ridgid');

INSERT INTO tool_rental_rate (tool_name, daily_amt, weekday_charge, weekend_charge, holiday_charge) VALUES ('Ladder', 1.99, true, true, false);
INSERT INTO tool_rental_rate (tool_name, daily_amt, weekday_charge, weekend_charge, holiday_charge) VALUES ('Chainsaw', 1.49, true, false, true);
INSERT INTO tool_rental_rate (tool_name, daily_amt, weekday_charge, weekend_charge, holiday_charge) VALUES ('Jackhammer', 2.99, true, false, false);