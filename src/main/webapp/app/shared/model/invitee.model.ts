export interface IInvitee {
  id?: number;
  status?: number;
  userProfileId?: number;
  eventEventName?: string;
  eventId?: number;
}

export const defaultValue: Readonly<IInvitee> = {};
