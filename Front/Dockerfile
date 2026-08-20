FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration=production

FROM nginx:alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/dist/sistema-gestao-atividades-complementares-front/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

RUN mkdir -p /var/run /run /var/cache/nginx /var/log/nginx /tmp/client_temp && \
    touch /run/nginx.pid /tmp/nginx.pid && \
    chown -R appuser:appgroup /usr/share/nginx/html /var/cache/nginx /var/log/nginx /etc/nginx /var/run /run /tmp && \
    chmod -R 777 /tmp /var/run /run

USER appuser
EXPOSE 8081

CMD ["nginx", "-g", "daemon off; pid /tmp/nginx.pid;", "-c", "/etc/nginx/nginx.conf"]
