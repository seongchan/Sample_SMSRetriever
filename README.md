Sample_SMSRetriever
=====================
Android SMS Retriever 샘플코드 입니다.

이 코드는 구글개발자 사이트에서 제공하는 기본 코드를 그대로! 사용하였고,
동작을 위한 몇가지 코드만 추가하였습니다.
자세한 내용은 아래의 사이트를 참고바랍니다.
[구글개발자 사이트:SMS Retriever API (https://developers.google.com/identity/sms-retriever/)](https://developers.google.com/identity/sms-retriever/)

위 사이트에서 안드로이드 구현 코드와 SMS 메시지 형식에 대한 내용을 참고하기 바랍니다.

## 1. 사이닝 hash 키
인증된 SMS를 주고 받기 위해서는 사이닝 hash 키가 필요합니다.
이에 먼저 사이닝 키를 생성을 합니다.
생성된 사이닝 키를 이용하여 다음과 같이 11자리의 hash키를 생성합니다.
hash 키 생성은 구글 개발자 사이트의 내용을 따르면 되나, hash 키를 생성할때에 sha256sum 명령이 제공되지 않아 파이프 오류가 날 수 있습니다.
저의 경우는 다음의 명령을 이용하였습니다.
<pre><code>
keytool -exportcert -alias testKey -keystore testSigningKey | xxd -p | tr -d "[:space:]" | echo -n com.clipandbooks.sample.smsretriever `cat` | shasum -a 256 | tr -d "[:space:]-" | xxd -r -p | base64 | cut -c1-11
</code></pre>
* 가이드에 있는 sha256sum 대신 shasum -a 256 으로 대체해서 사용했다는 점만 다릅니다.

## 2. SMS 메시지 생성
인증된(?) SMS의 형식은 다음의 규칙을 따라야 합니다. [참고 : 1.Construct a verification message](https://developers.google.com/identity/sms-retriever/verify)

* Be no longer than 140 bytes
* Begin with the prefix <#>
* Contain a one-time code that the client sends back to your server to complete the verification flow

위 규칙에 따라서 SMS 메시지는 다음과 같이 생성합니다.

<pre><code><#> Test Auth Message 1234 XGw4Kt/c2qe </code></pre>

맨 마지막에 있는 값이 사이닝 hash 키입니다.

## 3. 테스트
사이닝키로 빌드된 앱을 설치 후 "START" 버튼을 누릅니다.
이 상태에서 앞에서 생성한 SMS 메시지를 앱이 설치된 폰으로 문자메시지를 전송합니다.
일반 문자메시지와 동일하게 메시지가 수신되고 앱 화면에서는 수신된 메시지 내용이 출력됩니다.

## 4. 추후에 고려해야 할 사항
이 앱은 SMS Retriever 에 대한 예제 코드입니다. 실제 서비스에 적용하기에는 아주 많은 사항이 빠져 있습니다.

### 1) 메시지 수신대기
서버에 SMS 발송 요청 후 메시지 수신이나 타임아웃될때까지 대기하는 코드는 작성되어 있지 않습니다. (이 부분은 실제 적용할 서비스에 맞춰서 고려해야 함)
타임아웃 시간을 따로 지정되어 있지 않습니다. (지원되는지 여부는 따로 확인하지 못함)

### 2) SMS 발송 요청 및 메시지 발송
실제 서비스에서는 메시지 발송요청 후 요청 수신완료가 된것이 확인되면 메시지 대기하는 형태로 작성해야 합니다.
별도 SMS 메시지 발송이 가능한 서버측 준비가 된다면 실제 서버를 통해서 일련의 과정을 진행하는 Flow를 작성해야 합니다.

## 5. 제약사항
외부 기관의 본인인증 문자 메시지를 이용하는 경우에는 이 방법이 적합하지 않을 수 있습니다.
여기에 소개된 내용은 SMS 수신에 대한 권한이 제한됨에 따라서 앱에 허용된 메시지만 수신하기 위해서 쓰이는 방법입니다.
(본인인증 자체에 대한 방법은 아님)
외부 기관에 본인인증 메시지 발송요청 시에 구글이 정한 형식에 맞춰서 (<#>로 시작하고 메시지 뒤에 hash 키 포함) 발송이 가능하다면 가능할 수 있습니다.

## 6. 라이센스
여기 있는 코드는 원래 참조했던 내용을 제외하고, 제가 작성한 부분에 대해서는 퍼블릭 도메인입니다.
[라이센스보기](LICENSE)
