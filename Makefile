# make local-up(정의한 명령어)

# -d: 백그라운드 실행, --force-recreate: 강제 재생성
local-up:
	docker-compose -f docker-compose-local.yml up -d --force-recreate

# -v: volume 삭제
local-down:
	docker-compose -f docker-compose-local.yml down -v

# docker exec -it community-redis redis-cli : 도커 레디스 커맨드라인 접속 명령어
# docker exec -it community-database bash : 도커 MySql Shell 접속 명령어
