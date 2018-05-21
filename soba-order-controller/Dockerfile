FROM openjdk:8-jdk-slim
LABEL maintainer="kazusato"
RUN useradd kube && \
 mkdir -p /home/kube && \
 chown kube:kube /home/kube
ADD jjug-lt-spring2018-all.jar /home/kube
USER kube
WORKDIR /home/kube
ENTRYPOINT ["java", "-cp", "jjug-lt-spring2018-all.jar", "kazusato.jjug.spring2018.kubeapi.SobaOrderControllerKt"]
