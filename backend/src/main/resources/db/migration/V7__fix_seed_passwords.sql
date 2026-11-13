UPDATE users
SET password_hash = '$2a$10$tss6OihsqIulP.VrZ/SGyuXJHhMjDKc4WdaH9wc/JzFKClNIBCfzC'
WHERE username IN ('admin', 'seller1', 'seller2');