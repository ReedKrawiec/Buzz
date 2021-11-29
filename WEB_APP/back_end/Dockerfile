FROM python:3.10.0-buster

WORKDIR /usr/src/app
COPY requirements.txt .
RUN pip install -r requirements.txt

COPY . .
COPY static static

CMD ["python","-u","-m","flask","run","--host=0.0.0.0"]