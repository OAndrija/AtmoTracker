# Use the official Node.js image as the base image
FROM node:16-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies specified in package.json
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Ensure app.js has execute permissions
RUN chmod +x app.js

# Expose the port the backend runs on
EXPOSE 3001

# Set the command to run the backend when the container starts
CMD ["node", "app.js"]