# Base Nginx image
FROM nginx:alpine

# Remove default config and copy custom one
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy your static site content
COPY html/ /usr/share/nginx/html/

# Expose port
EXPOSE 80
