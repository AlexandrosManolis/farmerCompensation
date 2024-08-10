# entrypoint.sh
#!/bin/bash

# Fetch the public IP address
IP=$(curl -s http://checkip.amazonaws.com/)

# Export the environment variable
export VITE_BACKEND_URL="http://$IP:9090"

# Execute the main command
exec "$@"
