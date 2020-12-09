SET /P NAME=<appname.txt
heroku logs --app %NAME% > ../logs/console.log