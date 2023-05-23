FROM jenkins/jenkins:lts
USER root
RUN apt-get update && \
    apt-get install -y python3
RUN python3 --version
RUN apt-get install -y python3-pip
RUN pip install --upgrade pip
RUN python3 -m pip install ansible
RUN ansible-galaxy collection install community.aws
RUN pip install botocore
RUN pip install boto3
USER jenkins
