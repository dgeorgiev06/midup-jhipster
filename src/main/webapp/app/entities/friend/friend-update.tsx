import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUserProfile } from 'app/shared/model/user-profile.model';
import { getEntities as getUserProfiles } from 'app/entities/user-profile/user-profile.reducer';
import { getEntity, updateEntity, createEntity, reset } from './friend.reducer';
import { IFriend } from 'app/shared/model/friend.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IFriendUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IFriendUpdateState {
  isNew: boolean;
  friendRequestingId: string;
  friendAcceptingId: string;
}

export class FriendUpdate extends React.Component<IFriendUpdateProps, IFriendUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      friendRequestingId: '0',
      friendAcceptingId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (!this.state.isNew) {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getUserProfiles();
  }

  saveEntity = (event, errors, values) => {
    values.friendshipRequestDate = new Date(values.friendshipRequestDate);
    values.friendshipStartDate = new Date(values.friendshipStartDate);

    if (errors.length === 0) {
      const { friendEntity } = this.props;
      const entity = {
        ...friendEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/friend');
  };

  render() {
    const { friendEntity, userProfiles, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="midupApp.friend.home.createOrEditLabel">Create or edit a Friend</h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : friendEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">ID</Label>
                    <AvInput id="friend-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="statusLabel" for="status">
                    Status
                  </Label>
                  <AvField id="friend-status" type="text" name="status" />
                </AvGroup>
                <AvGroup>
                  <Label id="friendshipRequestDateLabel" for="friendshipRequestDate">
                    Friendship Request Date
                  </Label>
                  <AvInput
                    id="friend-friendshipRequestDate"
                    type="datetime-local"
                    className="form-control"
                    name="friendshipRequestDate"
                    value={isNew ? null : convertDateTimeFromServer(this.props.friendEntity.friendshipRequestDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="friendshipStartDateLabel" for="friendshipStartDate">
                    Friendship Start Date
                  </Label>
                  <AvInput
                    id="friend-friendshipStartDate"
                    type="datetime-local"
                    className="form-control"
                    name="friendshipStartDate"
                    value={isNew ? null : convertDateTimeFromServer(this.props.friendEntity.friendshipStartDate)}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="friendRequesting.id">Friend Requesting</Label>
                  <AvInput id="friend-friendRequesting" type="select" className="form-control" name="friendRequestingId">
                    <option value="" key="0" />
                    {userProfiles
                      ? userProfiles.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label for="friendAccepting.id">Friend Accepting</Label>
                  <AvInput id="friend-friendAccepting" type="select" className="form-control" name="friendAcceptingId">
                    <option value="" key="0" />
                    {userProfiles
                      ? userProfiles.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/friend" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">Back</span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp; Save
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  userProfiles: storeState.userProfile.entities,
  friendEntity: storeState.friend.entity,
  loading: storeState.friend.loading,
  updating: storeState.friend.updating,
  updateSuccess: storeState.friend.updateSuccess
});

const mapDispatchToProps = {
  getUserProfiles,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FriendUpdate);
