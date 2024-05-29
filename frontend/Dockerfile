# Use the official Node.js image as the base image
FROM node:14-alpine

# Install build dependencies
RUN apk add --no-cache python3 make g++ bash

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies specified in package.json
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Fix permissions
RUN chmod -R 755 /app

# Build the React app
RUN npm run build

# Install serve to serve the build directory
RUN npm install -g serve

# Expose the port the app runs on
EXPOSE 3000

# Command to run the app
CMD ["serve", "-s", "build", "-l", "3000"]
