const publicIdContainer = document.querySelector('#main-publicId');
const usernameContainer = document.querySelector('#main-user');
let pUsername = document.createElement("p");
let pPublicId = document.createElement("p");

  function axiosTest() {
    const api = axios.create({
    	withCredentials: true
    });

            api.get('http://localhost:8080/user/get').then(res => {
                console.log(res);
                const publicId = res.data.publicId;
                const user = res.data.username;
                pPublicId.innerHTML = 'PublicID: ' + publicId;
                publicIdContainer.appendChild(pPublicId);
                pUsername.innerHTML = 'Username: ' + user;
                usernameContainer.appendChild(pUsername);
          })
    }
axiosTest();

