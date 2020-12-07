SET /P NAME=<appname.txt
heroku ps:scale worker=1 --app %NAME%