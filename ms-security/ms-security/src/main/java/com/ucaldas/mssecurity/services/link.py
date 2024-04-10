from flask import Flask, request, jsonify
from azure.communication.email import EmailClient

# Crea una instancia de la aplicación Flask
app = Flask(__name__)

# Define una ruta para enviar correos electrónicos
@app.route('/send_email', methods=['POST'])
def send_email():
    try:
        # Se obtiene la información del cuerpo de la solicitud
        data = request.json

        # Se extraen los datos necesarios para enviar el correo electrónico
        sender_address = data.get('sender_address')
        recipient_address = data.get('recipient_address')
        subject = data.get('subject')
        plain_text = data.get('plain_text')

        # Aquí va tu lógica de envío de correo electrónico
        connection_string = "endpoint=https://hkma-notificaciones.unitedstates.communication.azure.com/;accesskey=1Hvrk2Kl5lFn5O/5oYX/60Rz1zduUGVSnCG+7GQ4MeWl8XgJ5Es0sdn6fb67EkyH7rC1Poahjv9dtVj9xqB6DQ=="
        client = EmailClient.from_connection_string(connection_string)

        message = {
            "senderAddress": sender_address,
            "recipients": {
                "to": [{"address": recipient_address}],
            },
            "content": {
                "subject": subject,
                "plainText": plain_text,
            }
        }

        poller = client.begin_send(message)
        result = poller.result()

        return jsonify({'message': 'Email sent successfully'})

    except Exception as ex:
        return jsonify({'error': str(ex)}), 500

# Ejecuta la aplicación Flask
if __name__ == '__main__':
    app.run (debug=True)
