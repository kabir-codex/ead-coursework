-- Update payments to spread across 5 months in 2026
-- So the dashboard bar chart shows multiple bars

UPDATE payments SET payment_date = '2026-01-15' WHERE payment_id = 1;
UPDATE payments SET payment_date = '2026-02-10' WHERE payment_id = 2;
UPDATE payments SET payment_date = '2026-02-14' WHERE payment_id = 3;
UPDATE payments SET payment_date = '2026-03-05' WHERE payment_id = 4;
UPDATE payments SET payment_date = '2026-04-10' WHERE payment_id = 5;
UPDATE payments SET payment_date = '2026-05-20' WHERE payment_id = 6;

-- Also insert a few extra payments to make chart look richer
INSERT INTO payments (booking_id, amount, payment_method, payment_date, notes) VALUES
(1, 15000.00, 'CASH',          '2026-01-20', 'Additional service charge'),
(2, 20000.00, 'CARD',          '2026-03-15', 'Extra nights'),
(3, 30000.00, 'BANK_TRANSFER', '2026-04-05', 'Package upgrade'),
(4, 25000.00, 'ONLINE',        '2026-05-10', 'Room upgrade'),
(5, 10000.00, 'CASH',          '2026-02-28', 'Tour extras');

