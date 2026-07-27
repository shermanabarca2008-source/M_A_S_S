-- Cuenta de administrador inicial para poder ingresar al sistema.
-- Todo el resto de datos (especialidades, médicos, pacientes) se ingresan desde la aplicación.
INSERT INTO administrador (usuario, contrasena) VALUES ('admin', 'admin123');

-- Paciente de prueba para verificar el inicio de sesión
INSERT INTO paciente (cedula, nombre_completo, correo_electronico, contrasena) 
VALUES ('0101234567', 'Juan Pérez', 'juan.perez@test.com', 'paciente123');
