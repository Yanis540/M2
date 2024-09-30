FROM node:20-alpine
RUN mkdir -p app
WORKDIR /app
COPY . .
RUN npm install
EXPOSE 5000
CMD ["npm","run","start"]