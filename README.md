Yurt Yönetim Sistemi Projesi
Bu proje, bir öğrenci yurdu ortamındaki öğrenci kaydı, personel yönetimi, oda atamaları ve izin talebi süreçlerini yönetmek için tasarlanmış bir Masaüstü (Desktop) uygulamasıdır. Uygulama, Java ve JavaFX kullanılarak Model-View-Controller (MVC) prensipleri etrafında geliştirilmiş olup, temiz kod ve sürdürülebilirlik sağlamak için Tasarım Desenleri uygulamaktadır.
Temel Özellikler
•	Güvenli Kimlik Doğrulama: Şifre hash'leme ile güvenli giriş ve kayıt.
•	Rol Bazlı Erişim: Öğrenci ve Personel rolleri ile farklı arayüz ve yetkilendirme.
•	Kullanıcı Yönetimi: Personel tarafından yeni kullanıcı ekleme ve öğrenci bilgilerini güncelleme.
•	Oda Yönetimi: Oda oluşturma, öğrenci yerleştirme ve oda durumu takibi.
•	İzin Yönetimi: Öğrenci tarafından izin talebi oluşturma ve Personel tarafından bu taleplerin onay/red süreci.
•	Dinamik Arama/Filtreleme: Öğrenci listelerinde dinamik arama mekanizmaları.
	Uygulamanın Mimarisi
Proje, Katmanlı Mimari prensibine sıkı sıkıya bağlıdır ve üç ana katmana ayrılmıştır:
Katman	Sorumluluk
Controller	Kullanıcı girdisini alır, UI'ı yönetir ve Service katmanını çağırır.
Service	İş mantığını yürütür, Repository'leri koordine eder.
Repository	Veritabanı erişimini soyutlar, JDBC işlemlerini yönetir.
Model	Uygulamanın verilerini temsil eder.
Uygulanan Tasarım Desenleri
Projenin modülerliğini ve sürdürülebilirliğini artıran temel tasarım desenleri:
1. Singleton (Tekil Örnek) Deseni
Temel Amaç: Bir sınıfın yalnızca tek bir örneğinin (instance) var olmasını garanti etmek ve bu örneğe global bir erişim noktası sağlamaktır.
Uygulamadaki İşlevi:
•	Veritabanı Bağlantı Yönetimi: DatabaseConnection.java sınıfı Singleton olarak tasarlanmıştır. Bu, uygulamanın yaşam döngüsü boyunca yalnızca tek bir veritabanı bağlantısı kurmasını ve kullanmasını sağlar. Bu, kaynak israfını önler ve bağlantı havuzunun temelini oluşturur.
•	Repository Erişimi: Tüm JDBC Repository'leri (JdbcStudentRepository, JdbcRoomRepository, JdbcPermissionRepository) Singleton'dır. Bu, her yerden aynı Repository nesnesine erişim garantisi vererek, veritabanı erişiminde tutarlılık ve merkezi kontrol sağlar.
•	Observer Sistemi: PermissionSubject.java (Observer/Gözlemci Deseni'nin yayıncısı), Singleton olarak tasarlanmıştır. Bu, tüm Observer'ların (StudentController, StaffController) tek ve aynı bildirim mekanizmasına abone olmasını ve güncellemeleri kaçırmamasını garanti eder.
2. Strategy (Strateji) Deseni
Temel Amaç: Çalışma anında arama algoritmalarını dinamik olarak değiştirmektir.
Uygulamadaki İşlevi: Bu desen, arama ve filtreleme işlemi yapılırken kullanılır. Kullanıcı, arama kriterini ("TC Numarası", "Oda Numarası" veya "İsim/Soyisim") seçtiğinde, Strateji dinamik olarak değişir. Örneğin, "TC Numarası" seçildiğinde new TCSearch() nesnesi oluşturulur. Bu strateji nesnesi, arama işlemini başlatması için RoomService'e gönderilir. Bu, farklı arama yöntemlerini kontrolör koduna dokunmadan uygulamamızı sağlar.
3. Observer (Gözlemci) Deseni
Temel Amaç: Durum değişikliklerini (olayları) anında ve gevşek bağlı bir şekilde ilgili bileşenlere bildirmektir.
Uygulamadaki İşlevi: Bu desen, izin yönetimi akışında gerçek zamanlı iletişim için kullanılır. Bir öğrenci PermissionService üzerinden yeni bir izin kaydettiğinde, PermissionSubject hemen bir bildirim yayınlar. Bu bildirim, StaffController'daki (Personel Ekranı) refreshPermissions() metodunu otomatik olarak tetikleyerek beklemedeki izinler listesinin anında güncellenmesini sağlar. Aynı şekilde, personel bir izni onayladığında, StudentController'daki update() metodu tetiklenir ve öğrenciye anlık durum değişikliği bildirilir.
4. State (Durum) Deseni
Temel Amaç: Bir nesnenin (oda) iç durumuna göre davranışını değiştirmek ve bu durum mantığını ayrıştırmaktır.
Uygulamadaki İşlevi: Bu desen, oda doluluk yönetimi için kullanılır. Room.java nesnesi bir öğrenci yerleştirme (addOccupant()) isteği aldığında, bu isteği mevcut durum nesnesine (AvailableState.java veya FullState.java) devreder. Oda Dolu iken yerleştirme isteği gelirse, FullState yerleştirmeyi reddeder; Oda Müsait iken yerleştirme yapılır ve doluluk tam kapasiteye ulaşırsa durum otomatik olarak FullState'e geçer.
5. Builder (Kurucu) Deseni
Temel Amaç: Karmaşık nesnelerin (çok sayıda parametreye sahip olanlar) adım adım, güvenli ve okunaklı bir şekilde oluşturulmasını sağlamaktır.
Uygulamadaki İşlevi: Bu desen, Student, Staff, Room ve Izin dahil olmak üzere tüm Model sınıflarının oluşturulmasında kullanılır. Örneğin, OgrenciKullaniciYonetimiController'da yeni bir kullanıcı kaydı yapılırken, uzun ve karmaşık kurucular yerine zincirleme metotlar kullanılır: .firstName(ad).lastName(soyad).build(). Bu yöntem, kodun okunabilirliğini artırır ve nesne oluşturma güvenliğini sağlar.
6. Factory (Fabrika) Deseni
Temel Amaç: Nesne oluşturma (hangi somut sınıfın oluşturulacağını seçme) mantığını merkezi bir yere taşımak ve bu mantığı tüketiciden gizlemektir.
Uygulamadaki İşlevi: Bu desen, Veri Haritalama (Mapping) sürecinde kullanılır. JdbcStudentRepository sınıfındaki mapUser() metodu, veritabanından çektiği genel kayıtları (ResultSet), Factory'ye (UserFactory.createUserFromResultSet()) gönderir. Fabrika, kaydın rolünü ("Öğrenci" veya "Personel") kontrol ederek doğru tipte (Student veya Staff) nesneyi oluşturur ve tüm verileri üzerine haritalar. Bu, Repository'yi karmaşık nesne oluşturma mantığından soyutlar.

Başlangıç ve Yapılandırma
1.	Veritabanı Kurulumu: DatabaseConnection.java dosyasındaki URL, USER ve PASSWORD değişkenlerini yerel SQL Server ayarlarınıza göre güncelleyin.
2.	Bağımlılıklar: Proje, JavaFX kütüphanesini ve JDBC sürücüsünü (bu durumda SQL Server) gerektirir.
3.	Başlatma: Uygulamayı controller dosyasında bulunan MainApp ile başlatın.

Proje Ekibi 
AD-SOYAD	GİTHUB LİNKİ

Azra AKBAŞ
	
https://github.com/AzraAkbas 


Sümeyra Nur KAYALAR	
https://github.com/SumeyraNurKayalar 


Ceyda AKMAN
	
https://github.com/CeydaAkman 


