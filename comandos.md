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

ansible-galaxy collection install community.aws
pip install botocore
pip install boto3

ansible-playbook -i localhost 05-playbook.yml