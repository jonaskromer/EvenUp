FROM sbtscala/scala-sbt:eclipse-temurin-25.0.1_8_1.12.0_3.7.4
RUN apt update && apt install -y \
    libgtk-3-0 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgl1 \
    xvfb \
    openjfx \
    && rm -rf /var/lib/apt/lists/*


RUN apt update && \
    apt install -y \
    xvfb \
    x11vnc

# Set environment variables for X11 forwarding
ENV DISPLAY=host.docker.internal:0
ENV LIBGL_ALWAYS_INDIRECT=true

# Create a directory for X11 UNIX socket
RUN mkdir -p /tmp/.X11-unix && chmod 1777 /tmp/.X11-unix

WORKDIR /evenup
ADD . /evenup

RUN sbt assembly

CMD ["java", "-Dprism.order=sw", "-jar", "target/scala-3.7.3/EvenUp.jar"]
