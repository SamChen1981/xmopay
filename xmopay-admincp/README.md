# 默认的profile为dev，其他环境通过指定启动参数使用不同的profile，比如：
#   测试环境：java -jar xmopay-admincp.jar --spring.profiles.active=dev
#   生产环境：java -jar xmopay-admincp.jar --spring.profiles.active=prod


nohup java -jar xmopay-admincp.jar --spring.profiles.active=prod --config.location=/opt/www/application.yml &