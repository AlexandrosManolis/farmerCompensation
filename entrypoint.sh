# entrypoint.sh
#!/bin/bash

# Fetch the public IP address
IP=$(curl ifconfig.me)

# Export the environment variable
export VITE_BACKEND_URL="http://$IP:9090"

# Export IP vlue
export SPRING_IP=$IP

# Execute the main command
exec "$@"