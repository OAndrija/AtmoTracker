# Use the official Node.js image as the base image
FROM node:20.12.0-alpine AS builder

# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies specified in package.json
RUN npm install

# Copy the rest of the application code to the working directory
COPY . .

# Change ownership of the app directory to the non-root user
RUN chown -R appuser:appgroup /app

# Temporarily switch back to root user to rebuild bcrypt
USER root

# Rebuild bcrypt to ensure the correct architecture
RUN npm rebuild bcrypt

# Change ownership back to non-root user after rebuild
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Ensure app.js has execute permissions
RUN chmod +x app.js

# Use a new stage to create the final image
FROM node:20.12.0-alpine

# Create a non-root user and group in the final image
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the built app from the builder stage
COPY --from=builder /app /app

# Change ownership of the app directory to the non-root user
RUN chown -R appuser:appgroup /app

# Switch to the non-root user
USER appuser

# Expose the port the backend runs on
EXPOSE 3001

# Set the command to run the backend when the container starts
CMD ["node", "app.js"]
