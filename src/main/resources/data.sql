INSERT INTO vehicle (vehicle_number, owner_name, vehicle_type, owner_phone, created_at)
SELECT 'TN01AB1234', 'Ravi Kumar', 'Car', '9876543210', NOW()
WHERE NOT EXISTS (SELECT 1 FROM vehicle WHERE vehicle_number = 'TN01AB1234');

INSERT INTO vehicle (vehicle_number, owner_name, vehicle_type, owner_phone, created_at)
SELECT 'TN02CD5678', 'Arun Kumar', 'Bike', '9123456789', NOW()
WHERE NOT EXISTS (SELECT 1 FROM vehicle WHERE vehicle_number = 'TN02CD5678');

INSERT INTO vehicle (vehicle_number, owner_name, vehicle_type, owner_phone, created_at)
SELECT 'TN03EF9012', 'Mani Kumar', 'Car', '9988776655', NOW()
WHERE NOT EXISTS (SELECT 1 FROM vehicle WHERE vehicle_number = 'TN03EF9012');

INSERT INTO violation_type (violation_code, violation_name, description, base_fine, created_at)
SELECT 'SPEED001', 'Speeding', 'Overspeeding violation', 1000, NOW()
WHERE NOT EXISTS (SELECT 1 FROM violation_type WHERE violation_code = 'SPEED001');

INSERT INTO violation_type (violation_code, violation_name, description, base_fine, created_at)
SELECT 'PARK001', 'Illegal Parking', 'Parking in restricted area', 500, NOW()
WHERE NOT EXISTS (SELECT 1 FROM violation_type WHERE violation_code = 'PARK001');

INSERT INTO violation_type (violation_code, violation_name, description, base_fine, created_at)
SELECT 'SIGNAL001', 'Signal Violation', 'Jumping traffic signal', 1500, NOW()
WHERE NOT EXISTS (SELECT 1 FROM violation_type WHERE violation_code = 'SIGNAL001');

INSERT INTO violation_type (violation_code, violation_name, description, base_fine, created_at)
SELECT 'HELMET001', 'No Helmet', 'Riding without helmet', 500, NOW()
WHERE NOT EXISTS (SELECT 1 FROM violation_type WHERE violation_code = 'HELMET001');
