/**
 * Your API Title
 *
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { GrantedAuthority } from './grantedAuthority';


export interface Users {
    username?: string;
    password?: string;
    roles?: Set<string>;
    enabled?: boolean;
    authorities?: Array<GrantedAuthority>;
    accountNonExpired?: boolean;
    credentialsNonExpired?: boolean;
    accountNonLocked?: boolean;
}

