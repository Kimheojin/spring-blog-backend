# 온프레미스 서버 스펙 정리

## 하드웨어 사양
- **CPU**: Intel(R) N100 (4 Cores / 4 Threads)
- **Memory**: 16 GB
- **Swap**: 4.0 GiB
- **Architecture**: x86_64

## 소프트웨어 사양
- **OS**: Ubuntu 24.04.3 LTS
- **Kernel**: Linux 6.8.0-62-generic

## 네트워크 구성
- **DDNS**: No-IP 
  - 공유기 public IP 고정 불가에 따른 외부 접속 도메인 관리
- **SSL/TLS**: HTTPS 
  - Let's Encrypt(CA) 인증서 적용
- **Local IP**: DHCP Static Lease 
  - 공유기 내 사설 IP 고정
- **Port Forwarding**
  - 80(HTTP), 443(HTTPS) 등 공유기 포트포워딩을을 통한 외부 개방

---
## 기타 관련 링크

[리드미로 이동](../README.md)
[부하테스트 관련 링크](../docsAboutMD/test2.md)