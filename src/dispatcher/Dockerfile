FROM golang:latest as builder

WORKDIR /app
COPY . ./
RUN go mod tidy
RUN CGO_ENABLED=0 GOOS=linux go build -mod=readonly  -v -o dispatcher

FROM alpine:3
RUN apk add --no-cache ca-certificates

COPY --from=builder /app/dispatcher /dispatcher
CMD ["/dispatcher"]
