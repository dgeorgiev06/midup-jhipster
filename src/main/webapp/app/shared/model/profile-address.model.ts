export interface IProfileAddress {
  id?: number;
  addressType?: number;
  address?: string;
  isDefault?: boolean;
  longitude?: number;
  latitude?: number;
  userProfileId?: number;
}

export const defaultValue: Readonly<IProfileAddress> = {
  isDefault: false
};
