from flask import Flask, request, jsonify
from azure.communication.email import EmailClient

# Crea una instancia de la aplicación Flask
app = Flask(__name__)

# Define una ruta para enviar correos electrónicos
@app.route('/send_email', methods=['POST'])
def send_email():
    try:
        data = request.json
        recipient_address = data.get('email')
        subject = data.get('subject')
        plain_text = data.get('plain_text')

        # Aquí debes ingresar tu cadena de conexión de Azure Communication Services
        connection_string = "endpoint=https://hkma-notificaciones.unitedstates.communication.azure.com/;accesskey=1Hvrk2Kl5lFn5O/5oYX/60Rz1zduUGVSnCG+7GQ4MeWl8XgJ5Es0sdn6fb67EkyH7rC1Poahjv9dtVj9xqB6DQ=="
        client = EmailClient.from_connection_string(connection_string)

        message = {
            "senderAddress": "DoNotReply@7ebed90e-32ff-41b7-968b-99da1740422d.azurecomm.net",
            "recipients": {
                "to": [{"address": recipient_address}],
            },
            "content": {
                "subject": "new password",
                "html": fr'<h2>{data.get("newPassword")}</h2>',
            }
        }

        poller = client.begin_send(message)
        result = poller.result()
        print(result)

        return jsonify({'message': 'Email sent successfully'})

    except Exception as ex:
        print(ex)
        return jsonify({'error': str(ex)}), 500

# Ejecuta la aplicación Flask
if __name__ == '__main__':
    app.run(debug=True)
