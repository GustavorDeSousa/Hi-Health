package br.com.thecharles.hihealth.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.thecharles.hihealth.config.SettingsFirebase;

public class UserFirebase {

    public static String getUId(){

        FirebaseAuth user = SettingsFirebase.getFirebaseAutenticacao();
//        String email = user.getCurrentUser().getEmail();
//        String id = user.getUid();
        String id = user.getCurrentUser().getUid();
//        String identificadorUsuario = Base64Custom.codificarBase64( email );

        return id;

    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth usuario = SettingsFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

//    public static boolean atualizarNomeUsuario(String nome){
//
//        try {
//
//            FirebaseUser user = getCurrentUser();
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setDisplayName( nome )
//                    .build();
//
//            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if( !task.isSuccessful() ){
//                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
//                    }
//                }
//            });
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//
//    }

//    public static boolean atualizarFotoUsuario(Uri url){
//
//        try {
//
//            FirebaseUser user = getCurrentUser();
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setPhotoUri( url )
//                    .build();
//
//            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if( !task.isSuccessful() ){
//                        Log.d("Perfil", "Erro ao atualizar foto de perfil.");
//                    }
//                }
//            });
//            return true;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//
//    }

//    public static Usuario getDadosUsuarioLogado(){
//
//        FirebaseUser firebaseUser = getCurrentUser();
//
//        Usuario usuario = new Usuario();
//        usuario.setEmail( firebaseUser.getEmail() );
//        usuario.setNome( firebaseUser.getDisplayName() );
//
//        if ( firebaseUser.getPhotoUrl() == null ){
//            usuario.setFoto("");
//        }else {
//            usuario.setFoto( firebaseUser.getPhotoUrl().toString() );
//        }
//
//        return usuario;
//}
}
