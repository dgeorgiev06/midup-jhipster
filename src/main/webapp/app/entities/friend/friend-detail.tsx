import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './friend.reducer';
import { IFriend } from 'app/shared/model/friend.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IFriendDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class FriendDetail extends React.Component<IFriendDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { friendEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Friend [<b>{friendEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="status">Status</span>
            </dt>
            <dd>{friendEntity.status}</dd>
            <dt>
              <span id="friendshipRequestDate">Friendship Request Date</span>
            </dt>
            <dd>
              <TextFormat value={friendEntity.friendshipRequestDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="friendshipStartDate">Friendship Start Date</span>
            </dt>
            <dd>
              <TextFormat value={friendEntity.friendshipStartDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>Friend Requesting</dt>
            <dd>{friendEntity.friendRequestingId ? friendEntity.friendRequestingId : ''}</dd>
            <dt>Friend Accepting</dt>
            <dd>{friendEntity.friendAcceptingId ? friendEntity.friendAcceptingId : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/friend" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/friend/${friendEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ friend }: IRootState) => ({
  friendEntity: friend.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FriendDetail);
