curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
python3 get-pip.py --user
python3 -m pip -V
pip install --upgrade pip
python3 -m pip install ansible

mkdir -p ~/.aws
touch ~/.aws/credentials
[default]
aws_access_key_id = AKIAWZHTONJS6OOFC27B
aws_secret_access_key = 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
---

cat <<EOF >>~/.aws/credentials
[default]
aws_access_key_id = AKIATO7UJZDNO4CP7KVM
aws_secret_access_key = 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
EOF

---
ansible-galaxy collection install community.aws
pip install botocore
pip install boto3

ansible-playbook -i localhost 05-playbook.yml

# docker build -t 040500/jenkins-ansible:v1 .

### Instalación simbple
pipeline
credentials
git


## comandos en jenkins as shell task
jenkins@3f45c20f6cef:~$ cat <<EOF >>~/.aws/credentials
[default]
aws_access_key_id = AKIATO7UJZDNO4CP7KVM
aws_secret_access_key = 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
EOF

## Install kubernetes
cat <<EOF >> /etc/hosts
10.0.1.54 nodo-master.cluster.local nodo-master
10.0.1.213 nodo-dev.cluster.local nodo-dev
10.0.1.236 nodo-qa.cluster.local nodo-qa
EOF

cat <<EOF >>/etc/modules-load.d/containerd.conf
overlay br_netfilter
EOF
modprobe overlay sudo modprobe br_netfilter

cat <<EOF >>/etc/sysctl.d/99-kubernetes-cri.conf
net.bridge.bridge-nf-call-iptables = 1
net.ipv4.ip_forward = 1
net.bridge.bridge-nf-call-ip6tables = 1
EOF

sysctl --system 

apt-get update && sudo apt-get install -y containerd 

mkdir -p /etc/containerd

containerd config default | sudo tee /etc/containerd/config.toml

sed -i s/'SystemdCgroup = false'/'SystemdCgroup = true'/g /etc/containerd/config.toml

systemctl restart containerd 
systemctl status containerd 

apt-get update && sudo apt-get install -y apt-transport-https curl
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add - 

cat <<EOF >>/etc/apt/sources.list.d/kubernetes.list
deb https://apt.kubernetes.io/ kubernetes-xenial main
EOF

apt-get update 

apt-get install -y kubelet=1.24.1-00 kubeadm=1.24.1-00 kubectl=1.24.1-00

apt-mark hold kubelet kubeadm kubectl 





kubeadm init --cri-socket=/run/containerd/containerd.sock --kubernetes-version 1.24.1 
echo 'source <(kubectl completion bash)' >>~/.bashrc

kubeadm token create --print-join-command 