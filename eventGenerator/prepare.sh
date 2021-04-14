python3 -m pip install --upgrade pip
python3 -m pip install --user virtualenv
python3 -m venv env
source env/bin/activate
pip install wheel
pip install gspread
pip install oauth2client
python3 main.py