FROM alpine:3.7
LABEL maintainer="kazusato"
RUN cd /tmp && \
 wget https://dl.k8s.io/v1.10.0/kubernetes-client-linux-amd64.tar.gz && \
 tar xvfz kubernetes-client-linux-amd64.tar.gz && \
 mv kubernetes/client/bin/kubectl /usr/local/bin && \
 rm -rf kubernetes && \
 rm kubernetes-client-linux-amd64.tar.gz 
ENTRYPOINT ["/usr/local/bin/kubectl", "proxy"]

