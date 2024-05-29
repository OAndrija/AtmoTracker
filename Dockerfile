FROM python:3.8-slim

WORKDIR /app

COPY . /app

EXPOSE 80

# Define environment variable
ENV NAME World

CMD ["python", "app.py"]
