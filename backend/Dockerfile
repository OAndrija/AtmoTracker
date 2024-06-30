# Use the official Node.js image as the base image
FROM node:20.12.0-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies specified in package.json
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Rebuild bcrypt to ensure the correct architecture
RUN npm rebuild bcrypt

# Ensure app.js has execute permissions
RUN chmod +x app.js

# Use a new stage to create the final image
FROM node:20.12.0-alpine

WORKDIR /app

# Copy the built app from the builder stage
COPY --from=builder /app /app

# Expose the port the backend runs on
EXPOSE 3001

# Set the command to run the backend when the container starts
CMD ["node", "app.js"]
