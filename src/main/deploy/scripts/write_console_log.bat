SET /P NAME=<appname.txt
heroku logs --app %NAME% > console.log